package com.example.eirikur.professoroak;

/**
 * Created by Eirikur on 27/05/2016.
 */
public class Person {

    private String name;
    private int score;

    public Person(String name, int score){
        this.name = name;
        this.score = score;
    }

    public String getName(){
        return name;
    }

    public int getScore(){
        return score;
    }

    public String toString(){
        return "Name: " + getName() + "\nScore: " + getScore();
    }
}
