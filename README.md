# Robustly Typed

> Robustness is the property of being strong and healthy in
> constitution. When it is transposed into a system, it refers to the
> ability of tolerating perturbations that might affect the system's
> functional body. In the same line robustness can be defined as "the
> ability of a system to resist change without adapting its initial
> stable configuration".

Define your data types and relationships with runtime and (in some
languages) compile time clarity.

Abstracts the data definitions from the implementation code.

# Usage

Given some YAML such as this:

```yaml
# scalar types - the name is part of the prop/type
scalars:
  name       : string
  first-name : name
  last-name  : name
  email      : [string, isEmail]
  age        : [int, isLiving]
  adult-age  : [age, isAdult]

# you can define entities as well
entities:
  person:
    - first-name
    - last-name
    - adult-age
    - email
```

It will generate entities that look like this:

```typescript
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
```

# Reasoning

As you can see - a tiny amount of the logic of what's going on
requires a ton of boilerplate to build a robust codebase that actually
attempts to guarantee data matches the definition(s) at compile and runtime.
