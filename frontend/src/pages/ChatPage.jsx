import React, { useState, useEffect, useRef } from 'react';
import api from '../api/api';
import { Send, Bot, User, Loader2, Plus, MessageSquare, History, Trash2 } from 'lucide-react';

const ChatPage = () => {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [sessions, setSessions] = useState([]);
  const [currentSessionId, setCurrentSessionId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [sessionsLoading, setSessionsLoading] = useState(true);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    fetchSessions();
  }, []);

  const fetchSessions = async () => {
    try {
      const response = await api.get('/sessions');
      setSessions(response.data.data);
    } catch (err) {
      console.error('Failed to fetch sessions', err);
    } finally {
      setSessionsLoading(false);
    }
  };

  const createNewSession = () => {
    setCurrentSessionId(null);
    setMessages([]);
  };

  const handleSend = async (e) => {
    e.preventDefault();
    if (!input.trim() || loading) return;

    const userMessage = { role: 'user', content: input };
    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setLoading(true);

    try {
      const response = await api.post('/chat/query', {
        query: input,
        sessionId: currentSessionId
      });

      const { answer, sessionId } = response.data.data;
      
      setMessages(prev => [...prev, { role: 'bot', content: answer }]);
      
      if (!currentSessionId) {
        setCurrentSessionId(sessionId);
        fetchSessions();
      }
    } catch (err) {
      setMessages(prev => [...prev, { role: 'bot', content: 'Sorry, I encountered an error. Please try again.' }]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex h-[calc(100vh-64px)] overflow-hidden">
      {/* Sidebar */}
      <div className="w-64 bg-base-200 border-r border-base-300 hidden md:flex flex-col">
        <div className="p-4">
          <button 
            onClick={createNewSession}
            className="btn btn-primary w-full gap-2"
          >
            <Plus size={18} /> New Chat
          </button>
        </div>
        <div className="flex-1 overflow-y-auto p-2 space-y-1">
          <div className="px-2 py-1 text-xs font-bold text-base-content/50 uppercase tracking-wider">Recent Chats</div>
          {sessionsLoading ? (
            <div className="flex justify-center p-4"><Loader2 className="animate-spin text-primary" size={24} /></div>
          ) : sessions.map((session) => (
            <button
              key={session.id}
              onClick={() => {
                setCurrentSessionId(session.id);
                // In a real app, you'd fetch history for this session
                setMessages([]); 
              }}
              className={`w-full text-left px-3 py-2 rounded-lg text-sm flex items-center gap-3 transition-colors ${currentSessionId === session.id ? 'bg-primary text-primary-content' : 'hover:bg-base-300'}`}
            >
              <MessageSquare size={16} />
              <span className="truncate">{session.title}</span>
            </button>
          ))}
        </div>
      </div>

      {/* Main Chat Area */}
      <div className="flex-1 flex flex-col bg-base-100 relative">
        {/* Messages */}
        <div className="flex-1 overflow-y-auto p-4 space-y-6">
          {messages.length === 0 ? (
            <div className="h-full flex flex-col items-center justify-center text-center p-8">
              <div className="w-20 h-20 bg-primary/10 text-primary rounded-3xl flex items-center justify-center mb-6">
                <Bot size={40} />
              </div>
              <h1 className="text-4xl font-bold mb-4">How can I help you?</h1>
              <p className="text-base-content/60 max-w-md">
                Ask me anything about your uploaded documents. I'll use RAG to provide accurate answers.
              </p>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-12 w-full max-w-2xl">
                {['Summarize my documents', 'What are the key findings?', 'Find specific details', 'Analyze trends'].map((suggestion) => (
                  <button 
                    key={suggestion}
                    onClick={() => setInput(suggestion)}
                    className="btn btn-outline btn-sm normal-case justify-start h-auto py-3 px-4"
                  >
                    {suggestion}
                  </button>
                ))}
              </div>
            </div>
          ) : (
            messages.map((msg, i) => (
              <div key={i} className={`flex gap-4 ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
                {msg.role === 'bot' && (
                  <div className="w-8 h-8 rounded-lg bg-primary text-primary-content flex items-center justify-center flex-shrink-0 mt-1">
                    <Bot size={18} />
                  </div>
                )}
                <div className={`max-w-[80%] p-4 rounded-2xl ${msg.role === 'user' ? 'bg-primary text-primary-content rounded-tr-none' : 'bg-base-200 rounded-tl-none border border-base-300'}`}>
                  <p className="whitespace-pre-wrap">{msg.content}</p>
                </div>
                {msg.role === 'user' && (
                  <div className="w-8 h-8 rounded-lg bg-neutral text-neutral-content flex items-center justify-center flex-shrink-0 mt-1">
                    <User size={18} />
                  </div>
                )}
              </div>
            ))
          )}
          {loading && (
            <div className="flex gap-4 justify-start">
              <div className="w-8 h-8 rounded-lg bg-primary text-primary-content flex items-center justify-center flex-shrink-0 mt-1">
                <Bot size={18} />
              </div>
              <div className="bg-base-200 p-4 rounded-2xl rounded-tl-none border border-base-300 flex items-center gap-2">
                <span className="loading loading-dots loading-sm"></span>
              </div>
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>

        {/* Input Area */}
        <div className="p-4 border-t border-base-300 bg-base-100/80 backdrop-blur-sm">
          <form onSubmit={handleSend} className="max-w-4xl mx-auto flex gap-2">
            <input
              type="text"
              placeholder="Ask a question..."
              className="input input-bordered flex-1 focus:outline-primary"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              disabled={loading}
            />
            <button 
              type="submit" 
              className="btn btn-primary btn-square"
              disabled={loading || !input.trim()}
            >
              <Send size={20} />
            </button>
          </form>
          <p className="text-[10px] text-center mt-2 text-base-content/40 uppercase tracking-widest font-bold">
            Personal RAG Assistant • AI can make mistakes
          </p>
        </div>
      </div>
    </div>
  );
};

export default ChatPage;
