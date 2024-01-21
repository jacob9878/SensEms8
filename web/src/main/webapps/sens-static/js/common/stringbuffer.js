/**
 * Created by Administrator on 2018-03-29.
 */
// StringBuffer
var StringBuffer = function() {
    this.buffer = new Array();
    this.buffer.r
};

StringBuffer.prototype.append = function(str) {
    this.buffer[this.buffer.length] = str;
};

StringBuffer.prototype.toString = function() {
    return this.buffer.join("");
};
