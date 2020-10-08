package com.example.fragment.Model;

import java.util.List;

public class CoordList {

    ////////////////
    private List<CoordinateSet> coordinateSet = null;

    public List<CoordinateSet> getCoordList() {
        return coordinateSet;
    }
    public void setCoordList(List<CoordinateSet> coordList) {
        this.coordinateSet = coordList;
    }
    ///////////////////
    public class Coordinate {
        private Double lat;

        private Double _long;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLong() {
            return _long;
        }

        public void setLong(Double _long) {
            this._long = _long;
        }
    }
    public class CoordinateSet{
        private List<Coordinate> Coordinate = null;

        public List<Coordinate> getCoordinateSet() {
            return Coordinate;
        }

        public void setCoordinateSet(List<Coordinate> Coordinate) {
            this.Coordinate = Coordinate;
        }
    }
}

