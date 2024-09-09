const functions = require('firebase-functions');
const admin = require('firebase-admin');
const axios = require('axios');

admin.initializeApp();

exports.updatePrices = functions.pubsub.schedule('every 24 hours').onRun(async (context) => {
    const db = admin.firestore();
    const productsRef = db.collection('products');

    try {
        // Obțineți toate documentele din colecția 'products'
        const snapshot = await productsRef.get();

        // Parcurgeți fiecare document și actualizați prețul
        snapshot.forEach(async (doc) => {
            try {
                // Obțineți ID-ul produsului din document
                const productId = doc.data().ID;

                // Configurați parametrii pentru cererea către API-ul de prețuri
                const params = new URLSearchParams();
                params.append('source', 'amazon');
                params.append('country', 'de');
                params.append('values', productId);

                // Configurați opțiunile pentru cererea către API-ul de prețuri
                const options = {
                    method: 'POST',
                    url: 'https://price-analytics.p.rapidapi.com/search-by-id',
                    headers: {
                        'content-type': 'application/x-www-form-urlencoded',
                        'X-RapidAPI-Key': '49deef18dbmshb95d5ad34ade815p18d838jsn2e51675c5762',
                        'X-RapidAPI-Host': 'price-analytics.p.rapidapi.com'
                    },
                    data: params,
                };

                // Faceți cererea către API-ul de prețuri
                const response = await axios.request(options);
                
                // Extrageți prețul din răspunsul API-ului
                const newPrice = response.data.price;

                // Actualizați prețul pentru documentul curent în Firestore
                await productsRef.doc(doc.id).update({ price: newPrice });

                console.log(`Prețul pentru produsul cu ID-ul ${productId} a fost actualizat cu succes.`);
            } catch (error) {
                console.error(`Eroare la actualizarea prețului pentru produsul cu ID-ul ${productId}:`, error);
            }
        });

        console.log('Actualizarea prețurilor finalizată cu succes.');
    } catch (error) {
        console.error('Eroare la obținerea datelor despre produse:', error);
    }
});
