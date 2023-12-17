import { GearIcon, MagnifyingGlassIcon } from "@radix-ui/react-icons";
import { Suspense, useEffect, useState } from "react";
import { getChats } from "../../api/chats";
import { Chat, Message, User } from "../../api/entities";
import { getLastMessageFromChat } from "../../api/messages";
import { findUsers, getUser } from "../../api/users";
import SidebarItem from "../SidebarItem";
import "./style.scss";

interface SidebarItemData extends Chat {
    username?: string;
    lastMessage?: Message;
}

const Sidebar = ({
    user,
    currentChat,
    setCurrentChat,
    settingsVisible,
    setSettingsVisible,
}: SidebarProps) => {
    const [search, setSearch] = useState<string>("");
    const [width, setWidth] = useState(320);

    const [userChats, setUserChats] = useState<Chat[]>([]);
    const [chats, setChats] = useState<SidebarItemData[]>([]);

    const fetchData = async () => {
        let chats: SidebarItemData[] = await getChats();

        for (let i = 0; i < chats.length; i++) {
            if (chats[i].users.length === 2) {
                const otherUserId = chats[i].users.find(
                    (userId) => user?.id !== userId
                );

                if (!otherUserId) {
                    return;
                }

                const otherUser = await getUser(otherUserId);

                chats[i].username = otherUser?.nickname;
                chats[i].avatarUrl = otherUser?.avatarUrl;
                chats[i].name = otherUser?.name;
            }

            chats[i].lastMessage = await getLastMessageFromChat(chats[i].id);
        }

        setUserChats(chats);

        return chats;
    };

    useEffect(() => {
        fetchData();
    }, [user]);

    useEffect(() => {
        fetchData().then((userChats) => {
            setChats(userChats ?? []);
        });
    }, []);

    useEffect(() => {
        const query = search.replace("@", "").toLowerCase();

        if (!query) {
            fetchData();
            setChats(userChats);
            return;
        }

        const globalSearch = async () => {
            const users = await findUsers(query);

            const newChats = users.map((foundUser) => {
                return {
                    id: -foundUser.id,
                    name: foundUser.name,
                    username: foundUser.nickname,
                    users: [user?.id, foundUser.id],
                    avatarUrl: foundUser.avatarUrl,
                    lastMessage: undefined,
                };
            }) as SidebarItemData[];

            const chatUserIds = new Set(
                userChats
                    .filter((chat) => chat.users.length === 2)
                    .flatMap((chat) => chat.users)
                    .filter((userId) => userId !== user?.id)
            );

            const uniqueChats = newChats.filter((chat) => {
                return !chatUserIds.has(Math.abs(chat.id));
            });

            setChats([...userChats, ...uniqueChats]);
        };

        globalSearch();
    }, [search]);

    const handleResize = (event: MouseEvent) => {
        const MAX_WIDTH = 640;

        if (event.clientX > width - 8) {
            const onMouseMove = (event: MouseEvent) => {
                setWidth(Math.min(Math.max(320, event.clientX), MAX_WIDTH));
            };
            const onMouseUp = () => {
                window.removeEventListener("mousemove", onMouseMove);
                window.removeEventListener("mouseup", onMouseUp);
            };
            window.addEventListener("mousemove", onMouseMove);
            window.addEventListener("mouseup", onMouseUp);
        }
    };

    if (!user) return null; // TODO: add loading state

    return (
        <nav
            className="sidebar"
            style={{ width: width }}
            onMouseDown={(
                event: React.MouseEvent<HTMLDivElement, MouseEvent>
            ) => {
                handleResize(event.nativeEvent as MouseEvent);
            }}
        >
            <div className="sidebar__search">
                <MagnifyingGlassIcon width={20} height={20} />
                <input
                    type="text"
                    placeholder="Search"
                    value={search}
                    onChange={(event) => setSearch(event.target.value)}
                />
            </div>
            <Suspense fallback={<div>Loading...</div>}>
                <div className="sidebar__chats">
                    {chats
                        .filter(
                            (chat) =>
                                chat.name.toLowerCase().includes(search) ||
                                (chat.username &&
                                    (
                                        "@" + chat.username.toLowerCase()
                                    ).includes(search))
                        )
                        .map((chat) => (
                            <SidebarItem
                                name={chat.name}
                                avatarUrl={chat.avatarUrl}
                                username={chat.username}
                                lastMessage={chat.lastMessage}
                                key={chat.id}
                                selected={chat.id === currentChat}
                                onClick={() => setCurrentChat(chat.id)}
                            />
                        ))}
                </div>
                <div className="sidebar__profile">
                    <img
                        className="sidebar__profile__avatar"
                        src={user.avatarUrl}
                        alt="avatar"
                    />
                    <div className="sidebar__profile__info">
                        <div className="sidebar__profile__info__name">
                            {user.name}
                        </div>
                        <div className="sidebar__profile__info__username">
                            @{user.nickname}
                        </div>
                    </div>
                    <GearIcon
                        width={20}
                        height={20}
                        onClick={setSettingsVisible}
                        style={
                            settingsVisible
                                ? {
                                      transform: "rotate(90deg)",
                                      opacity: 1,
                                  }
                                : {}
                        }
                    />
                </div>
            </Suspense>
        </nav>
    );
};

interface SidebarProps {
    user: User | null;
    currentChat: number | null;
    setCurrentChat: (username: number) => void;
    settingsVisible: boolean;
    setSettingsVisible: () => void;
}

export default Sidebar;
