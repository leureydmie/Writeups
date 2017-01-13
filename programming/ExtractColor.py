import urllib
from PIL import Image

""" Writeup for the challenge https://www.zenk-security.com/display_epreuve.php?id=31 """

URL = "http://prog1.challenges.zenk-security.io/color.php?"

def extractcolor(image):
    pixels = image.convert('RGBA').load()
    r, g, b, a = pixels[0, 0]
    return rgb2hex(r, g, b)


def rgb2hex(r, g, b):
    return '{:02x}{:02x}{:02x}'.format(r, g, b)

with open("color.bmp", "wb") as image:
    image.write(urllib.urlopen(URL).read())
    image.close()

challenge = Image.open("color.bmp")
result = extractcolor(challenge)
responseUrl = URL + "reponse=" + str(result)
print responseUrl
print(urllib.urlopen(responseUrl).read())
