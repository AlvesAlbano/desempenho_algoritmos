import random

tamanhos = [
    5_000,
    50_000,
    100_000,
    500_000,
    1_000_000,
]

for idx, tamanho in enumerate(tamanhos):

    lista = list(range(tamanho))
    
    random.shuffle(lista)
    
    with open(f"dados{str(tamanhos[idx])}.txt", "w") as file:
        for numero in lista:
            file.write(f"{numero}\n")
