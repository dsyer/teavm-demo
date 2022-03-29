with import <nixpkgs> { };

mkShell {

  name = "env";
  buildInputs = [
    nodejs python3
  ];

  shellHook = ''
    echo TeaVM Demo
  '';

}