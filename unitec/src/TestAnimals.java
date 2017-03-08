public class TestAnimals {
  public static void main(String[] args) {
    Fish f = new Fish();
    Cat c = new Cat("Fluffy");
    Animal a = new Fish();
    Animal e = new Spider();
    Pet p = new Cat();

    // Demonstrate different implementations of an interface
    f.play();
    c.play();

    // Demonstrate virtual method invocation
    e.eat();
    e.walk();

    // Demonstrate calling super methods
    a.walk();
  }
}
//////////////////////////////////////////////////////////////
abstract class Animal {
  protected int legs;

  protected Animal(int legs) {
    this.legs = legs;
  }

  public abstract void eat();

  public void walk() {
    System.out.println("This animal walks on " + legs + " legs.");
  }

}

//////////////////////////////////////////////////////////////
class Cat extends Animal implements Pet {
  private String name;

  public Cat(String name) {
    super(4);
    this.name = name;
  }
  public Cat() {
    this("");
  }

  public void setName(String name) {
    this.name = name;
  }
  public String getName() {
    return name;
  }
  public void play() {
    System.out.println(name + " likes to play with string.");
  }

  public void eat() {
    System.out.println("Cats like to eat spiders and mice.");
  }
}

//////////////////////////////////////////////////////////////
class Fish extends Animal implements Pet {
  private String name;

  public Fish() {
    super(0);   // this line must be here
  }

  public void setName(String name) {
    this.name = name;
  }
  public String getName() {
    return name;
  }
  public void play() {
    System.out.println("Fish swim in their tanks all day.");
  }

  public void walk() {
    super.walk();
    System.out.println("Fish, of course, can't walk; they swim.");
  }
  public void eat() {
    System.out.println("Fish eat pond scum.");
  }
}

//////////////////////////////////////////////////////////////
class Spider extends Animal {

  public Spider() {
    super(8);
  }

  public void eat() {
    System.out.println("Spiders catch flies in their webs to eat.");
  }
}

//////////////////////////////////////////////////////////////
interface Pet {
  public void setName(String name);
  public String getName();
  public void play();
}

//////////////////////////////////////////////////////////////