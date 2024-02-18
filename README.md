# az-kviz

[Reagent](https://reagent-project.github.io) view component for [AZ-kvíz](https://cs.wikipedia.org/wiki/AZ-kv%C3%ADz) board.

## Usage

Include as a dependency: `[net.mynarz.az-kviz "0.1.4"]`

Copy `resources/public/css/style.css` to your project's CSS folder for the base design.

Require the component `net.mynarz.az-kviz.view/board`. When you call it, you need to provide it with two arguments: configuration and board state.

The configuration allows you to customize how the board looks and behaves. You can see the default values of the configuration in `net.mynarz.az-kviz.view/board-config`. The most important of it is `:on-click`, where you can provide a function that handles clicks on the board tiles. The function's argument is the ID of the clicked tile, which corresponds to the tile's index in the board state.

The board state represents the state of a game. You can get an initial board state by `net.mynarz.az-kviz.logic/init-board-state`, and probably store it in a Reagent atom. The board state is a vector of tile states, each tracking the tile's `:status` and the `:text` or custom `:svg` in [Hiccup syntax](https://github.com/weavejester/hiccup) (wrapped in `viewBox="0 0 100 100"`) shown. You can control the looks of a board tile (an [SVG `g`](https://developer.mozilla.org/en-US/docs/Web/SVG/Element/g)) by setting its CSS `:classes`. By default, it has the `.tile` class and a class named after its `:status`.

To determine a game's winner, call `net.mynarz.az-kviz.logic/who-won` on the board state.

## Development

To get a ClojureScript REPL with live code reloading via [figwheel-main](https://figwheel.org), run `lein fig:dev`. To execute the tests, run `lein fig:test`.
