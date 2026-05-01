import client from "./client";

export const getSessions = () => client.get("/sessions");

export const createSession = (title) =>
  client.post(`/sessions?title=${encodeURIComponent(title)}`);

export const sendQuery = ({ query, session_id, session_title }) =>
  client.post("/chat/query", { query, session_id, session_title });
