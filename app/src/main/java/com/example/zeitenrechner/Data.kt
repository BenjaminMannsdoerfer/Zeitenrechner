package com.example.zeitenrechner

class Data {
    var id : Int = 0
    var name : String = ""
    var time : String = ""
    var distance : String = ""
    var paceM : String = ""
    var paceH : String = ""

    constructor(name:String, time:String, distance: String, paceM: String, paceH:String) {
        this.name = name
        this.time = time
        this.distance = distance
        this.paceM = paceM
        this.paceH = paceH
    }

    constructor() {

    }

    constructor(name:String) {
        this.name = name
    }

}