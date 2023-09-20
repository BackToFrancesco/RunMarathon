# RunMarathon
![image](https://github.com/BackToFrancesco/RunMarathon/assets/76614857/da959551-5b3e-43a1-bed2-85e62f4264b8)




This project was developed as final project for Wireless Networks for Mobile Applications 2022/2023 held by Prof. Claudio Palazzi in Univeristy of Padova.
## Authors
Francesco Bacchin, University of Padua, Italy

[Leila Dardouri](https://github.com/leidard), University of Padua, Italy


## RunMarathon
RunMarathon" is an Android application developed using Kotlin. This application uses the [Android Beacon Library](https://altbeacon.github.io/android-beacon-library/) to detect beacon signals emitted by transmitters along a marathon route to verify that all marathon checkpoints are crossed by an athlete. Additionally, it collects marathon completion times.
- For a detailed explanation, please refer to the [paper](https://github.com/BackToFrancesco/RunMarathon/blob/main/paper%20and%20presentation/RunMarathonPaper.pdf).
- Watch the [video](https://github.com/BackToFrancesco/RunMarathon/blob/main/paper%20and%20presentation/RunMarathonVideo.mp4) for a demonstration.

The application is designed to operate using a Firebase server to remotely store all the data received from each athlete participating in the marathon. For maintenance reasons related to the server, the code has been adapted for demonstration purposes so that the application can function without a server connection.
