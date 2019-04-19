package com.whf.demolist.database.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = DbPerson.Key.TABLE_NAME)
public class DbPerson {

    public interface Key {
        String TABLE_NAME = "tb_person";
    }

    @DatabaseField(id = true)
    private String idCard;

    @DatabaseField
    private String name;

    @DatabaseField
    private int age;

    @DatabaseField
    private int sex;

    @DatabaseField(uniqueCombo = true)
    private long birthday;

    @DatabaseField
    private String fatherIdCard;

    /**
     * 和 birthday 联合唯一是指同一个人同一瞬间只能生出 1 个人
     */
    @DatabaseField(uniqueCombo = true)
    private String motherIdCard;

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getFatherIdCard() {
        return fatherIdCard;
    }

    public void setFatherIdCard(String fatherIdCard) {
        this.fatherIdCard = fatherIdCard;
    }

    public String getMotherIdCard() {
        return motherIdCard;
    }

    public void setMotherIdCard(String motherIdCard) {
        this.motherIdCard = motherIdCard;
    }

    @Override
    public String toString() {
        return "DbPerson{" +
                "idCard='" + idCard + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                ", birthday=" + birthday +
                ", fatherIdCard='" + fatherIdCard + '\'' +
                ", motherIdCard='" + motherIdCard + '\'' +
                '}';
    }
}
