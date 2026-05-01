import { useState, useEffect, useRef } from "react";
import { getSessions } from "../api/chat";
import { sendQuery } from "../api/chat";
import { useToast } from "../context/ToastContext";

export default function ChatPage() {
  const [sessions, setSessions] = useState([]);
  const [activeSession, setActiveSession] = useState(null);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [sending, setSending] = useState(false);
  const messagesEnd = useRef(null);
  const toast = useToast();

  useEffect(() => {
    getSessions()
      .then(({ data }) => setSessions(data.data || []))
      .catch(() => {});
  }, []);

  useEffect(() => {
    messagesEnd.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleSend = async (e) => {
    e.preventDefault();
    const query = input.trim();
    if (!query || sending) return;

    const userMsg = { role: "user", content: query };
    setMessages((prev) => [...prev, userMsg]);
    setInput("");
    setSending(true);

    try {
      const payload = { query };

      if (activeSession) {
        payload.session_id = activeSession.id;
      } else {
        payload.session_title = query.slice(0, 50);
      }

      const { data } = await sendQuery(payload);
      const result = data.data;

      const assistantMsg = {
        role: "assistant",
        content: result.answer,
        sources: result.sources || [],
      };

      setMessages((prev) => [...prev, assistantMsg]);

      // refresh sessions to pick up new one
      if (!activeSession) {
        const { data: sessData } = await getSessions();
        const refreshed = sessData.data || [];
        setSessions(refreshed);
        if (refreshed.length > 0) {
          setActiveSession(refreshed[refreshed.length - 1]);
        }
      }
    } catch (err) {
      toast(err.response?.data?.message || "Query failed", "error");
      setMessages((prev) => prev.slice(0, -1)); // remove user msg on error
    } finally {
      setSending(false);
    }
  };

  const selectSession = (session) => {
    setActiveSession(session);
    setMessages([]);
  };

  const startNewChat = () => {
    setActiveSession(null);
    setMessages([]);
    setInput("");
  };

  return (
    <div className="chat-layout">
      {/* sidebar */}
      <aside className="chat-sidebar">
        <div className="sidebar-header">
          <button className="btn btn-ghost" onClick={startNewChat}>
            + New chat
          </button>
        </div>
        <div className="session-list">
          {sessions.map((s) => (
            <button
              key={s.id}
              className={`session-item ${activeSession?.id === s.id ? "active" : ""}`}
              onClick={() => selectSession(s)}
            >
              {s.title || "Untitled"}
            </button>
          ))}
        </div>
      </aside>

      {/* main */}
      <div className="chat-main">
        {messages.length === 0 ? (
          <div className="chat-empty">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
            </svg>
            <h3>Ask anything</h3>
            <p>Query your uploaded documents using natural language</p>
          </div>
        ) : (
          <div className="chat-messages">
            {messages.map((msg, i) => (
              <div key={i} className={`message ${msg.role}`}>
                <div className="message-bubble">{msg.content}</div>
                {msg.sources?.length > 0 && (
                  <div className="message-sources">
                    {msg.sources.map((s, j) => (
                      <span key={j} className="source-tag">{s}</span>
                    ))}
                  </div>
                )}
              </div>
            ))}
            {sending && (
              <div className="message assistant">
                <div className="message-bubble">
                  <span className="spinner" />
                </div>
              </div>
            )}
            <div ref={messagesEnd} />
          </div>
        )}

        <form className="chat-input-bar" onSubmit={handleSend}>
          <div className="chat-input-wrapper">
            <input
              className="chat-input"
              type="text"
              placeholder="Type your question…"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              disabled={sending}
              autoFocus
            />
            <button className="btn btn-primary" type="submit" disabled={sending || !input.trim()}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <line x1="22" y1="2" x2="11" y2="13" />
                <polygon points="22 2 15 22 11 13 2 9 22 2" />
              </svg>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
