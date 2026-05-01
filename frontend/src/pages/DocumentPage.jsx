import React, { useState, useEffect } from 'react';
import api from '../api/api';
import { Upload, File, Trash2, Loader2, Plus, CheckCircle, AlertCircle } from 'lucide-react';

const DocumentPage = () => {
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState(null);

  const fetchDocuments = async () => {
    try {
      const response = await api.get('/documents');
      setDocuments(response.data.data);
    } catch (err) {
      console.error('Failed to fetch documents', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDocuments();
  }, []);

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!file) return;

    setUploading(true);
    setMessage(null);
    const formData = new FormData();
    formData.append('file', file);

    try {
      await api.post('/documents/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      setMessage({ type: 'success', text: 'Document uploaded successfully!' });
      setFile(null);
      // Reset input
      e.target.reset();
      fetchDocuments();
    } catch (err) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Upload failed.' });
    } finally {
      setUploading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Are you sure you want to delete this document?')) return;
    try {
      await api.delete(`/documents/${id}`);
      setDocuments(documents.filter(doc => doc.id !== id));
    } catch (err) {
      console.error('Failed to delete document', err);
    }
  };

  return (
    <div className="container mx-auto p-4 md:p-8 max-w-4xl">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
        <div>
          <h1 className="text-3xl font-bold">My Documents</h1>
          <p className="text-base-content/60">Upload and manage documents for your RAG system</p>
        </div>
      </div>

      <div className="card bg-base-100 shadow-xl mb-8">
        <div className="card-body">
          <h2 className="card-title mb-4">Upload New Document</h2>
          <form onSubmit={handleUpload} className="flex flex-col md:flex-row gap-4 items-end">
            <div className="form-control w-full">
              <input 
                type="file" 
                className="file-input file-input-bordered w-full" 
                onChange={handleFileChange}
                accept=".pdf,.txt,.docx"
                required
              />
            </div>
            <button 
              type="submit" 
              className={`btn btn-primary min-w-[120px] ${uploading ? 'loading' : ''}`}
              disabled={uploading || !file}
            >
              {uploading ? <Loader2 className="animate-spin" size={18} /> : <Upload size={18} />}
              Upload
            </button>
          </form>
          {message && (
            <div className={`alert ${message.type === 'success' ? 'alert-success' : 'alert-error'} mt-4 py-2`}>
              {message.type === 'success' ? <CheckCircle size={18} /> : <AlertCircle size={18} />}
              <span>{message.text}</span>
            </div>
          )}
        </div>
      </div>

      <div className="grid gap-4">
        {loading ? (
          <div className="flex justify-center p-12">
            <Loader2 className="animate-spin text-primary" size={40} />
          </div>
        ) : documents.length === 0 ? (
          <div className="text-center p-12 bg-base-200 rounded-xl border-2 border-dashed border-base-300">
            <File className="mx-auto mb-4 text-base-content/20" size={48} />
            <p className="text-xl font-medium">No documents yet</p>
            <p className="text-base-content/50">Upload a PDF or text file to get started</p>
          </div>
        ) : (
          documents.map((doc) => (
            <div key={doc.id} className="card bg-base-100 shadow-md hover:shadow-lg transition-shadow border border-base-200">
              <div className="card-body p-4 flex-row items-center justify-between">
                <div className="flex items-center gap-4 overflow-hidden">
                  <div className="w-10 h-10 bg-primary/10 text-primary rounded-lg flex items-center justify-center flex-shrink-0">
                    <File size={20} />
                  </div>
                  <div className="overflow-hidden">
                    <h3 className="font-semibold truncate">{doc.name}</h3>
                    <p className="text-xs text-base-content/50">Uploaded: {new Date(doc.createdAt).toLocaleDateString()}</p>
                  </div>
                </div>
                <button 
                  onClick={() => handleDelete(doc.id)} 
                  className="btn btn-ghost btn-circle btn-sm text-error hover:bg-error/10"
                >
                  <Trash2 size={18} />
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default DocumentPage;
