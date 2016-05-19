package proffesoroak.westerdals.no.professoroak211;

/**
 * Created by Bruker on 19.05.2016.
 */
public class Pokemon {
    private Long _id;
    private String name;
    private double lat;
    private double lng;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        this._id = id;
    }

    public double getLng() {return lng;}

    public void setLng(float lng) {this.lng = lng;}

    public double getLat() {return lat;}

    public void setLat(float lat) {this.lat = lat;}

}
