with import <nixpkgs> { };

mkShell {

  name = "env";
  buildInputs = [
    nodejs python3
  ];

  shellHook = ''
    echo JSweet Demo
  '';

}