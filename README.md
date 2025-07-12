# velocity

Velocity is an application starter kit for Clojure that provides the perfect starting point for your next project.

## Features

- Application login, registration and email verification
- Session management
- User settings page, with the ability to change name, email address and password
- Pretty DaisyUI powered interface
- Easy deployment via Docker

## Deploy

### Docker

```bash
# build the project
$ docker build -t velocity .

# run the project, if using with SQLite don't forget to map a volume to /app/data
$ docker run --rm -p 3000:3000 -v ./data:/app/data -e APP_REGISTER_ENABLED=true velocity

# The website is now available at localhost:3000
```

### Uberjar

```bash
# just build the uberjar using leiningen
$ lein uberjar

# execute the jar
$ APP_REGISTER_ENABLED=true java -jar ./target/uberjar/velocity-VERSION-standalone.jar

# The website is now available at localhost:3000
```

## License

MIT
