import client from "./client";

export const getDocuments = () => client.get("/documents");

export const uploadDocument = (file) => {
  const form = new FormData();
  form.append("file", file);
  return client.post("/documents/upload", form, {
    headers: { "Content-Type": "multipart/form-data" },
  });
};

export const deleteDocument = (id) => client.delete(`/documents/${id}`);
