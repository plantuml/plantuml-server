var AsciiEncoder = (function() {
	var encode6bits = new Array(64);
	var decode6bits = new Array(128);

	function append3bytes(arr, b1, b2, b3) {
		var c1 = b1 >> 2;
		var c2 = ((b1 & 0x3) << 4) | (b2 >> 4);
		var c3 = ((b2 & 0xF) << 2) | (b3 >> 6);
		var c4 = b3 & 0x3F;
		arr.push(encode6bits[c1 & 0x3F]);
		arr.push(encode6bits[c2 & 0x3F]);
		arr.push(encode6bits[c3 & 0x3F]);
		arr.push(encode6bits[c4 & 0x3F]);
	}

	function decode3bytes(arr, cc1, cc2, cc3, cc4) {
		var c1 = decode6bits[cc1];
		var c2 = decode6bits[cc2];
		var c3 = decode6bits[cc3];
		var c4 = decode6bits[cc4];
		arr.push((c1 << 2) | (c2 >> 4));
		arr.push(((c2 & 0x0F) << 4) | (c3 >> 2));
		arr.push(((c3 & 0x3) << 6) | c4);
	}

	function charAt(s, i) {
		if (i >= s.length) {
			return '0';
		}
		return s.charAt(i);
	}

    function encode(data) {
        if (!data) {
             return "";
        }
        var arr = [];
        for (var i = 0; i < data.length; i += 3) {
             append3bytes(arr, data[i] & 0xFF, i + 1 < data.length ? data[i + 1] & 0xFF : 0,
                     i + 2 < data.length ? data[i + 2] & 0xFF : 0);
        }
        return arr.join('');
    }

    function decode(s) {
        var data = [];
        for (var i = 0; i < s.length; i += 4) {
             decode3bytes(data, charAt(s, i), charAt(s, i + 1), charAt(s, i + 2), charAt(s, i + 3));
        }
        return data;
    }

    function decode6bit(c) {
        return decode6bits[c];
    }

    function encode6bit(b) {
        if (b < 10) {
             return String.fromCharCode('0'.charCodeAt(0) + b);
        }
        b -= 10;
        if (b < 26) {
             return String.fromCharCode('A'.charCodeAt(0) + b);
        }
        b -= 26;
        if (b < 26) {
             return String.fromCharCode('a'.charCodeAt(0) + b);
        }
        b -= 26;
        if (b == 0) {
             return '-';
        }
        if (b == 1) {
             return '_';
        }
        return '?';
    }

    for (var i = 0; i < 64; ++i) {
         encode6bits[i] = encode6bit(i);
         decode6bits[encode6bits[i]] = i;
    }

    return {
        encode: encode,
        decode: decode,
	    encode6bit: encode6bit,
	    decode6bit: decode6bit
    };

}());
