type name = string
type first_name = name
type last_name = name
type email = string
type age = number
type adult_age = age

interface person {
  first_name: first_name
  last_name: last_name
  adult_age: adult_age
  email: email
}

function assertIs (s, f) {
  if (false === f(s)) throw new Error('Assert failed on value: ' + s)
}

function assertAll (s, fs) {
  fs.map(assertIs.bind(s))
}

const isEmail = s => /.*@.*/.test(s)
const isLiving = n => n > 0 && n < 150
const isAdult = n => n > 17

class im_person implements person {
  public static fromSerial (s) {
    const {
      first_name,
      last_name,
      adult_age,
      email,
    } = JSON.parse(s)

    return new this(
      first_name,
      last_name,
      adult_age,
      email,
    )
  }

  constructor (
    public readonly first_name = '',
    public readonly last_name = '',
    public readonly adult_age = 0,
    public readonly email = '',
  ) {
    assertAll([isEmail], email)
    assertAll([isLiving, isAdult], adult_age)
  }

  public toSerial (x: {} = {}) {
    return JSON.stringify({ ...this as {}, ...x })
  }

  public clone (x: {}) {
    return im_person.fromSerial(this.toSerial(x))
  }
}

const p = new im_person()

console.log(p)
console.log(im_person.fromSerial(p.toSerial()))
console.log(p.clone({ adult_age: 36 }))
