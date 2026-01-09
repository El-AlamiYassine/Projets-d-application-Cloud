FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Create app directory
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

# Copy JADE library
COPY lib/jade.jar ./lib/jade.jar

# Copy source files
COPY . .

# Compile the Java files
RUN javac -cp "./lib/jade.jar:./" *.java

# Expose port for JADE platform
EXPOSE 1099 7778

# Run the application
CMD ["java", "-cp", "./lib/jade.jar:./", "Main"]