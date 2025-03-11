package jreader;

import edu.stanford.nlp.coref.data.Dictionaries.Gender;

public class SpeakerEntity {

    private Integer corefClusterID;
    private Gender gender;

    public SpeakerEntity(){
        this.corefClusterID = -1;
        this.gender = Gender.UNKNOWN;
    }

    public void setGender(Gender newGender){
        gender = newGender;
    }

    public void setCorefClusterID(Integer newCorefClusterID){
        corefClusterID = newCorefClusterID;
    }

    public Gender getGender(){
        return gender;
    }

    public Integer getCorefClusterID(){
        return corefClusterID;
    }
}
