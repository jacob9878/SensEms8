Map = function() {
    this.map = new Object();
};
Map.prototype = {
    put : function(key, value) {
        this.map[key] = value;
    },
    get : function(key) {
        if( this.containsKey( key ) ){
            return this.map[key];
        }
        return "";
    },
    containsKey : function(key) {
        return key in this.map;
    },
    containsValue : function(value) {
        for ( var prop in this.map) {
            if (this.map[prop] == value)
                return true;
        }
        return false;
    },
    isEmpty : function(key) {
        return (this.size() == 0);
    },
    clear : function() {
        for ( var prop in this.map) {
            delete this.map[prop];
        }
    },
    remove : function(key) {
        delete this.map[key];
    },
    keys : function() {
        var keys = new Array();
        for ( var prop in this.map) {
            keys.push(prop);
        }
        return keys;
    },
    values : function() {
        var values = new Array();
        for ( var prop in this.map) {
            values.push(this.map[prop]);
        }
        return values;
    },
    size : function() {
        var count = 0;
        for ( var prop in this.map) {
            count++;
        }
        return count;
    },
    toString : function() {
        var str = "{";
        var keys = this.keys();
        for ( var i = 0; i < keys.length; i++) {
            if (i != 0) {
                str += ",";
            }
            str += '"';
            str += keys[i];
            str += '"';
            str += ":";
            if (this.get(keys[i])) {
                str += '"';
                str += this.get(keys[i]);
                str += '"';
            } else {
                str += "null";
            }
        }
        str += "}";
        return str;
    },
    serialize : function(){
        var str = "";
        var keys = this.keys();
        for ( var i = 0; i < keys.length; i++) {
            if (i != 0) {
                str += "&";
            }
            if (this.get(keys[i])) {
                if( this.get(keys[i]) instanceof Array ){
                    var array = this.get(keys[i]);
                    for(var j = 0 ; j < array.length ; j++){
                        if (j != 0) {
                            str += "&";
                        }
                        str += keys[i]+"="+array[j];
                    }
                }else{
                    str += keys[i]+"="+this.get(keys[i]);
                }
            } else {
                str += keys[i]+"=";
            }
        }
        return str;
    },
    toArray:function(){
        var array = new Array();
        var keys = this.keys();
        for ( var i = 0; i < keys.length; i++) {
            array.push([keys[i],this.get(keys[i])]);
        }
        return array;
    },
    clone:function(){
        var obj = new Map();
        var keys = this.keys();
        for ( var i = 0; i < keys.length; i++) {
            obj.put( keys[i] , this.get(keys[i]) );
        }
        return obj;
    }
};