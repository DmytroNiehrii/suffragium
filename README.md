How to build:
https://github.com/spotify/docker-maven-plugin

You can build an image with the above configurations by running this command.

mvn clean package docker:build
To push the image you just built to the registry, specify the pushImage flag.

mvn clean package docker:build -DpushImage
To push only specific tags of the image to the registry, specify the pushImageTag flag.

mvn clean package docker:build -DpushImageTag