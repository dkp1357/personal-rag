import { useState, useEffect, useRef } from "react";
import { getDocuments, uploadDocument, deleteDocument } from "../api/documents";
import { useToast } from "../context/ToastContext";

export default function DocumentPage() {
  const [docs, setDocs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [dragOver, setDragOver] = useState(false);
  const fileRef = useRef(null);
  const toast = useToast();

  const fetchDocs = async () => {
    try {
      const { data } = await getDocuments();
      setDocs(data.data || []);
    } catch {
      toast("Failed to load documents", "error");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDocs();
  }, []);

  const handleUpload = async (file) => {
    if (!file) return;
    setUploading(true);
    try {
      await uploadDocument(file);
      toast("Document uploaded");
      await fetchDocs();
    } catch (err) {
      toast(err.response?.data?.message || "Upload failed", "error");
    } finally {
      setUploading(false);
      if (fileRef.current) fileRef.current.value = "";
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteDocument(id);
      setDocs((prev) => prev.filter((d) => d.id !== id));
      toast("Document deleted");
    } catch {
      toast("Delete failed", "error");
    }
  };

  const onDrop = (e) => {
    e.preventDefault();
    setDragOver(false);
    const file = e.dataTransfer.files?.[0];
    if (file) handleUpload(file);
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <div>
          <h1 className="page-title">Documents</h1>
          <p className="page-description">Upload files to build your knowledge base</p>
        </div>
      </div>

      <div
        className={`upload-zone ${dragOver ? "drag-over" : ""}`}
        onDragOver={(e) => { e.preventDefault(); setDragOver(true); }}
        onDragLeave={() => setDragOver(false)}
        onDrop={onDrop}
        onClick={() => fileRef.current?.click()}
      >
        <input
          ref={fileRef}
          type="file"
          onChange={(e) => handleUpload(e.target.files?.[0])}
          tabIndex={-1}
        />
        <svg className="upload-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
          <polyline points="17 8 12 3 7 8" />
          <line x1="12" y1="3" x2="12" y2="15" />
        </svg>
        <p className="upload-text">
          {uploading ? "Uploading…" : <><strong>Click to upload</strong> or drag & drop</>}
        </p>
      </div>

      {loading ? (
        <div className="doc-empty"><span className="spinner" /></div>
      ) : docs.length === 0 ? (
        <div className="doc-empty">No documents yet — upload one above</div>
      ) : (
        <div className="doc-list">
          {docs.map((doc) => (
            <div className="doc-item" key={doc.id}>
              <div className="doc-info">
                <div className="doc-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                    <polyline points="14 2 14 8 20 8" />
                  </svg>
                </div>
                <div>
                  <div className="doc-name">{doc.fileName || doc.file_name || doc.name}</div>
                  <div className="doc-meta">
                    {doc.createdAt ? new Date(doc.createdAt).toLocaleDateString() : ""}
                  </div>
                </div>
              </div>
              <button className="btn btn-danger btn-sm" onClick={() => handleDelete(doc.id)}>
                Delete
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
