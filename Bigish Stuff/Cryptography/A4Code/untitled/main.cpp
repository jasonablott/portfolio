#include <iostream>

int main() {
    // int x = pow(2,0x987654321);
    // int y = x%0x123456789d;
    // std::cout << y << std::endl;
    int a  =68719476739;
    int b = 0x987654321;
    int c = 0x123456789d;
    int d = pow(a,b);
    int e = d%c;
    std::cout << e << std::endl;
}
