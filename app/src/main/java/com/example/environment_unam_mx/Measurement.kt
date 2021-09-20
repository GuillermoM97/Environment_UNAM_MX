package com.example.environment_unam_mx

class Measurement {
    var measure = ""
    var n_of_cough = 0
    var n_of_sneeze = 0
    var locinfo = ""
    var aux1 = 0
    var time_read = ""
    var date_read = ""
    constructor(measure:String, n_of_cough:Int, n_of_sneeze:Int,locinfo:String, aux1:Int, time_read:String, date_read:String){
        this.measure = measure
        this.n_of_cough = n_of_cough
        this.n_of_sneeze = n_of_sneeze
        this.locinfo = locinfo
        this.aux1 = aux1
        this.time_read = time_read
        this.date_read = date_read

    }
}