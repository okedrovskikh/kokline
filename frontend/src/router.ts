import { RootRoute, Route, Router } from "@tanstack/react-router";
import App from "./routes/app";
import Login from "./routes/login";
import Signup from "./routes/signup";

const rootRoute = new RootRoute();

const indexRoute = new Route({
    getParentRoute: () => rootRoute,
    path: "/",
    component: App,
});

const loginRoute = new Route({
    getParentRoute: () => rootRoute,
    path: "/login",
    component: Login,
});

const signupRoute = new Route({
    getParentRoute: () => rootRoute,
    path: "/signup",
    component: Signup,
});

const routeTree = rootRoute.addChildren([indexRoute, loginRoute, signupRoute]);

const router = new Router({ routeTree, defaultPreload: "intent" });

declare module "@tanstack/react-router" {
    interface Register {
        router: typeof router;
    }
}

export { router };
