./gradlew build

docker build --platform linux/amd64 -t gnidinger/assignment:0.0.1 .

docker push gnidinger/assignment:0.0.1
