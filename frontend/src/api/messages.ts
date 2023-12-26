import { Message, UnauthorizedError } from "./entities";

export const getLastMessageFromChat = async (
    chatId: number
): Promise<Message> => {
    const response = await fetch(
        `${
            import.meta.env.VITE_API_URL
        }/messages/fromChat/${chatId}?page=0&pageSize=1`,
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
        throw new UnauthorizedError(response.statusText);
    }

    return (await response.json())[0];
};

export const getAllMessagesFromChat = async (
    chatId: number
): Promise<Message[]> => {
    const response = await fetch(
        `${import.meta.env.VITE_API_URL}/messages/fromChat/${chatId}/all`,
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
        throw new UnauthorizedError(response.statusText);
    }

    return response.json();
};

export const getMessagesFromChat = async (
    chatId: number,
    page: number,
    pageSize: number
): Promise<Message[]> => {
    const response = await fetch(
        `${
            import.meta.env.VITE_API_URL
        }/messages/fromChat/${chatId}?page=${page}&pageSize=${pageSize}`,
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
        throw new UnauthorizedError(response.statusText);
    }

    return response.json();
};

export const getMessage = async (messageId: number): Promise<Message> => {
    const response = await fetch(
        `${import.meta.env.VITE_API_URL}/messages/${messageId}`,
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
        throw new UnauthorizedError(response.statusText);
    }

    return response.json();
};
