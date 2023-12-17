# Messenger app

This is a simple messenger app that allows users to send messages to each other.  
It is built using React and Vite, with Ktor as the backend.

## Requirements

-   Node.js
-   Yarn or npm

## Environment variables

The following environment variables are required in `.env` file:

```properties
# Change this to the URL of the backend
VITE_API_URL=http://localhost:8080/api/v1
VITE_WS_URL=ws://localhost:8080

# Cloudinary credentials
VITE_CLOUDINARY_URL=
VITE_CLOUDINARY_PRESET=
```

## Cloudinary

This app uses Cloudinary to store images. In order to be able to upload images, you need to create a new upload preset in Cloudinary settings/Upload section. Also, you need to set Signing mode to Unsigned. After that, you need to set the preset name in the environment variable `VITE_CLOUDINARY_PRESET` and the URL in `VITE_CLOUDINARY_URL`.

## Running the app

To run the app, you need to run the following commands:

```bash
# Install dependencies
yarn install

# Run the app
yarn run dev
```

This will start the development server at `localhost:5173`.

## Building the app

To build the app, you need to run the following commands:

```bash
# Install dependencies
yarn install

# Build the app
yarn run build
```

This will create a production build in the `dist` folder.

## Todo

-   [x] Base routes
    -   [x] Login page
    -   [x] Register page
    -   [x] App page
-   [x] User settings modal
-   [ ] Sidebar
    -   [x] Search
    -   [x] All chats
    -   [x] User info
    -   [ ] Chat creation
    -   [ ] Websocket integration
-   [x] Cloudinary integration
-   [x] Chat page
    -   [x] Chat header
    -   [x] Chat messages
    -   [x] Chat input
    -   [x] Websocket integration
-   [ ] Chat sidebar
    -   [ ] Chat info
    -   [ ] Chat actions
    -   [ ] Chat members
-   [ ] Default avatar

### Planned features

-   [ ] Chat search
-   [ ] Image support

## Used libraries

-   [React](https://react.dev/)
-   [Vite](https://vitejs.dev/)
-   [Sass](https://sass-lang.com/)
-   [Radix Icons](https://www.radix-ui.com/icons/)
-   [Tanstack Router](https://tanstack.com/router/v1)
