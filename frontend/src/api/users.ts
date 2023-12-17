import { NotFoundError, UnauthorizedError, User } from "./entities";

export const getMe = async (): Promise<User> => {
    const user = JSON.parse(sessionStorage.getItem("user")!);

    if (!user) {
        throw new UnauthorizedError("Local user not found");
    }

    const response = await fetch(`${import.meta.env.VITE_API_URL}/users/me`, {
        method: "GET",
        headers: {
            Accept: "application/json",
        },
        credentials: "include",
        mode: "cors",
    });

    if (!response.ok) {
        throw new UnauthorizedError(response.statusText);
    }

    return response.json();
};

export const getUser = async (userId: number): Promise<User> => {
    const response = await fetch(
        `${import.meta.env.VITE_API_URL}/users/${userId}`,
        {
            method: "GET",
            headers: {
                Accept: "application/json",
            },
            credentials: "include",
            mode: "cors",
        }
    );

    if (!response.ok) {
        throw new NotFoundError(response.statusText);
    }

    return response.json();
};

export const getUsers = async (): Promise<User[]> => {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/users`, {
        method: "GET",
        headers: {
            Accept: "application/json",
        },
    });

    if (!response.ok) {
        throw new NotFoundError(response.statusText);
    }

    return response.json();
};

export const findUsers = async (query: string): Promise<User[]> => {
    const response = await fetch(
        `${import.meta.env.VITE_API_URL}/users?search=${query}`,
        {
            method: "GET",
            headers: {
                Accept: "application/json",
            },
        }
    );

    if (!response.ok) {
        throw new NotFoundError(response.statusText);
    }

    return response.json();
};

export const updateUser = async (body: BodyInit): Promise<void> => {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/users`, {
        method: "PUT",
        headers: {
            Accept: "application/json",
            "Content-Type": "application/json",
        },
        body: body,
        credentials: "include",
        mode: "cors",
    });

    if (!response.ok) {
        throw new UnauthorizedError(response.statusText);
    }

    return;
};

export const logout = async (): Promise<void> => {
    const response = await fetch(
        `${import.meta.env.VITE_API_URL}/auth/logout`,
        {
            method: "POST",
            credentials: "include",
            mode: "cors",
        }
    );

    if (!response.ok) {
        throw new UnauthorizedError(response.statusText);
    }

    sessionStorage.clear();
};

export const deleteAccount = async (): Promise<void> => {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/users`, {
        method: "DELETE",
        credentials: "include",
        mode: "cors",
    });

    if (!response.ok) {
        throw new UnauthorizedError(response.statusText);
    }
};
