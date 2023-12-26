export class NotFoundError extends Error {}
export class UnauthorizedError extends Error {}

export interface User {
    id: number;
    name: string;
    nickname: string;
    avatarUrl?: string;
    chats?: number[];
}

export interface Message {
    id: number;
    chatId: number;
    userId: number;
    payload: string;
    timestamp: Date;
}

export interface Chat {
    id: number;
    name: string;
    avatarUrl?: string;
    users: number[];
}
