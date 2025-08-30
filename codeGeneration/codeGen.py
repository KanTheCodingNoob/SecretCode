import random

def generate_strings(count=1000, length=18, letters="BACXIU", filename="generated_strings.txt"):
    # Generate random strings
    strings = [''.join(random.choices(letters, k=length)) for _ in range(count)]

    for i in range (1, 1000):
        print(strings[i] + " ")

if __name__ == "__main__":
    generate_strings()