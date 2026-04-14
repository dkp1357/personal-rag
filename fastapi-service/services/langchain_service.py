from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_core.prompts import PromptTemplate

from langchain_core.output_parsers import StrOutputParser

from config import config


class LangChainService:
    def __init__(self):
        self.llm = ChatGoogleGenerativeAI(
            model="gemini-2.5-flash",
            temperature=0.1,
            max_output_tokens=512,  # max tokens in output
            google_api_key=config.GEMINI_API_KEY,
        )

        self.prompt = PromptTemplate.from_template(
            """Use ONLY the provided context to answer the question.
If the answer is not explicitly in the context, say "I don't know".
Do not infer or make up information.

Context:
{context}

Question:
{question}

Answer:"""
        )

        self.chain = self.prompt | self.llm | StrOutputParser()

    def generate(self, query: str, context_chunks: list[str]):
        try:
            if not context_chunks:
                return "I don't know."

            context = "\n\n--\n\n".join(context_chunks)

            return self.chain.invoke({"context": context, "question": query})

        except Exception as e:
            return f"Error generating response: {str(e)}"
