const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();


/**
 * Given the id of the marathon, it returns the ids of the first and
 * of the last beacons of that marathon.
 */
exports.getFirstAndLastBeacon = functions.https.onRequest((req, res) => {
  res.set('Content-Type', 'application/json');

  const dataReceived = req.body.data;
  const marathonRef = admin.database().ref(`/marathon/${dataReceived.idMarathon}/beacons`);

  marathonRef.orderByChild("distance").once('value', (snapshot) => {
    const firstBeacon = Object.keys(snapshot.val())[0];
    const lastBeacon = Object.keys(snapshot.val())[Object.keys(snapshot.val()).length -1];
    
    res.send({data: {firstBeacon: firstBeacon, lastBeacon: lastBeacon} });
  });
});


/**
 * It checks if the marathon is valid by looping through the list of beacons
 * that the user detected during the race, comparing it with the list
 * containing the beacons placed among the path of the marathon.
 * If it is valid, it stores the data.
 */
exports.checkAllBeacons = functions.https.onRequest((req, res) => {
  res.set('Content-Type', 'application/json');

  const dataReceived = req.body.data;
  const marathonRef = admin.database().ref(`/marathon/${dataReceived.idMarathon}/beacons`);
  marathonRef.once("value", (snapshot) => {
    const idsBeaconsMarathon = Object.keys(snapshot.val());

    let i = 0;
    let j = 0;

    // check if all beacons are present
    while (i < idsBeaconsMarathon.length && j < dataReceived.beacons.length){
      if (idsBeaconsMarathon[i] == dataReceived.beacons[j].id) i++;
      j++;
    }
    j--;

    if (i == idsBeaconsMarathon.length && dataReceived.beacons[j].isTheLastOne){ // if marathon is valid
      // store all data
      admin.database().ref(`/marathon/${dataReceived.idMarathon}/data/${dataReceived.idUser}`).set(dataReceived); 

      const hour = dataReceived.beacons[j].durationTime.hour;
      const second = dataReceived.beacons[j].durationTime.second;
      const minute = dataReceived.beacons[j].durationTime.minute;
      
      // store data in ranking
      admin.database().ref(`/marathon/${dataReceived.idMarathon}/ranking/${dataReceived.idUser}`).set({
        totalDurationTime: (hour * 3600) + (minute * 60) + second,
        durationTime: {
          hour,
          minute,
          second,
        }
      }).catch((error) => {
        res.status(500).send({data: { response: { message: "Error saving data: ", error } }});
      });

      res.status(200).send({data: { response: { message: "All beacons found." } }});
      
    } else { // marathon not valid
      res.status(400).send({data: { response: { message: "Some beacons are missing." } }});
    }
    });
  }
);


/**
 * Given the idMarathon, returns the ranking.
 */
exports.getRanking = functions.https.onRequest((req, res) => {
  res.set('Content-Type', 'application/json');

  const dataReceived = req.body.data;
  const ranking = admin.database().ref(`/marathon/${dataReceived.idMarathon}/ranking`);

  ranking.once("value", (snapshot) => {
    const rankingData = snapshot.val();
    
    // map over the ranking
    const rankingWithNames = Object.keys(rankingData).map(async id => {
      const totalDurationTime = rankingData[id].totalDurationTime;

      const userSnapshot = await admin.database().ref(`/user/${id}`).once("value");
      const user = userSnapshot.val();

      return { 
        id, 
        totalDurationTime,
        name: user.name, 
        surname: user.surname };
    });

    Promise.all(rankingWithNames).then(values => {
      // sort in ascending order by totalDurationTime
      const sortedRanking = values.sort((a, b) => a.totalDurationTime - b.totalDurationTime);
      res.send({ data: { ranking: sortedRanking } });
    });
  });
});
