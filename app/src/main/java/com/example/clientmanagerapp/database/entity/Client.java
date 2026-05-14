package com.example.clientmanagerapp.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.clientmanagerapp.models.Sex;

@Entity(tableName = "clients") // Marks this class as a database table
public class Client {

    @PrimaryKey(autoGenerate = true)
    public int id; // It's good practice to have a primary key

    public String name;
    public Sex sex;
    public String birthDate;
    public String phone;
    public float height;
    public float weight;
    public int distribution;
    public boolean favorite;
    public long creationTimestamp;

    // This constructor matches the one you are calling in your fragment.
    public Client(String name, Sex sex, String birthDate, String phone, float height, float weight, int distribution, boolean favorite, long creationTimestamp) {
        this.name = name;
        this.sex = sex;
        this.birthDate = birthDate;
        this.phone = phone;
        this.height = height;
        this.weight = weight;
        this.distribution = distribution;
        this.favorite = favorite;
        this.creationTimestamp = creationTimestamp;
    }

    public String getName() { return name; }
    public Sex getSex() { return sex; }
    public String getBirthDate() { return birthDate; }
    public String getPhone() { return phone; }
    public float getHeight() { return height; }
    public float getWeight() { return weight; }
    public int getDistribution() { return distribution; }
    public boolean isFavorite() { return favorite; }
    public long getCreationTimestamp() { return creationTimestamp; }
    public int getId() { return id; }

    public void setName(String name) { this.name = name; }
    public void setSex(Sex sex) { this.sex = sex; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setHeight(float height) { this.height = height; }
    public void setWeight(float weight) { this.weight = weight; }
    public void setDistribution(int distribution) { this.distribution = distribution; }
    public void setCreationTimestamp(long creationTimestamp) { this.creationTimestamp = creationTimestamp; }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }


}
