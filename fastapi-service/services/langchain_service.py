from langchain_openai import ChatOpenAI
from langchain.prompts import PromptTemplate


class LangChainService:
    def __init__(self):
        self.llm = ChatOpenAI(model="gpt-4.1-mini", temperature=0)

        self.prompt = PromptTemplate.from_template(
            """Answer ONLY from the given context.

Context:
{context}

Question:
{question}

Answer:"""
        )

    def generate(self, query: str, context_chunks: list[str]):

        context = "\n\n".join(context_chunks)

        formatted_prompt = self.prompt.format(context=context, question=query)

        response = self.llm.invoke(formatted_prompt)

        return response.content
