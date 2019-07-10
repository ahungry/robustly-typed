type name = string
type first_name = name
type last_name = name
type email = string
type age = number

interface person {
  first_name: first_name
  last_name: last_name
  age: age
  email: email
}

function assertIs (f, s) {
  if (false === f(s)) throw new Error('Assert failed on value: ' + s)
}

const isEmail = (s) => /.*@.*/.test(s)

class im_person implements person {
  public static fromSerial (s) {
    const {
      first_name,
      last_name,
      age,
      email,
    } = JSON.parse(s)

    return new this(
      first_name,
      last_name,
      age,
      email,
    )
  }

  constructor (
    public readonly first_name = '',
    public readonly last_name = '',
    public readonly age = 0,
    public readonly email = '',
  ) {
    assertIs(isEmail, email)
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
console.log(p.clone({ age: 36 }))
