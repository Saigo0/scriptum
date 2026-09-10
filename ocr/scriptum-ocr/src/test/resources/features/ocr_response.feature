# language: pt
Funcionalidade: Contrato da resposta OCR
  A resposta do OCR deve expor os dados do documento
  e proteger a confiança informada.

  Cenário: Criar resposta com confiança válida
    Dado um documento 2026 com os parágrafos "Primeiro|Segundo"
    Quando a resposta OCR é criada com confiança "87.5"
    Então o identificador da resposta é 2026
    E a resposta contém 2 parágrafos
    E a confiança da resposta é "87.5"

  Esquema do Cenário: Rejeitar confiança fora do intervalo permitido
    Dado um documento 2026 com os parágrafos "Texto"
    Quando uma resposta OCR é criada com confiança inválida "<confianca>"
    Então a resposta é rejeitada por confiança inválida

    Exemplos:
      | confianca |
      | -0.1      |
      | 100.1     |
      | NaN       |
