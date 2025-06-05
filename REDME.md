# Lungge Java

This project is developed for my course work.

## Installation

1. Install Docker. The guide how to do this you can find [here](https://www.docker.com/get-started).
2. Clone this repository - `git clone https://github.com/LWJerri/lungge-java.git`.
3. Create `.env.dev` file in the root of project and paste content from `.env.dev.example` file.

## Run Application

1. Run `docker compose -f docker-compose.dev.yaml --env-file=.env.dev up -d` to start all necessary services, like Keycloak, PostgreSQL, etc.
2. Open `http://localhost:8088` page and enter credentials `dev/dev` (from `.env.dev` file).
3. Create a new realm named `lungge-realm` and enable it.
4. Go to `Clients` page and create a new client named `lungge-client`.
   4.1. On 2nd step you need to enable `Client authentication`.
   4.2. On 3rd step set root URL as `http://localhost:8080/` and set valid redirect URLs:

- `http://localhost:8080/token`
- `http://localhost:8080/login/oauth2/code/keycloak`
- `http://localhost:8080/`
  4.3. Set `Web Origins` to `http://localhost:8080`.
  4.4. Open `Users` page and create a new user with verified email.
  4.4.1. Open `Credentials` page for selected user and set password. Remove check from `Temporary Password`.

5. Open `Credentials` page, copy `Client Secret` value and set it to `src/main/resources/application.yml` in `client-secret` field.
6. Run Java app from `LunggeApplication.java` file.
7. Open `http://localhost:8080` and continue authorization.

## License

This code has **MIT** license. See the `LICENSE` file for getting more information.
