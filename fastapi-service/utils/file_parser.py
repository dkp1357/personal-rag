from pypdf import PdfReader


def parse_file(file_path):
    if file_path.endswith(".pdf"):
        pdf_reader = PdfReader(file_path)
        text = ""
        for page in pdf_reader.pages:
            text += page.extract_text() or ""
        return text

    elif file_path.endswith(".txt"):
        with open(file_path, "r", encoding="utf-8") as f:
            return f.read()

    else:
        raise Exception("unsupported file type")
