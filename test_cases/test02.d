var sum := 0;
var i, tmp, ass;

for i in 1 .. 10 loop
    tmp := sum;
    sum := tmp + i;
end;

print  sum;