# `#mq/'`

Started from a [question from Miikka on ClojureVerse](https://clojureverse.org/t/a-macro-between-quote-and-syntax-quote/4466): _"A macro between quote and syntax-quote"_

```clojure
;; What I want:
user=> (let [a 1] (magic-quote (let [x 0] (+ x ~a))))
(let [x 0] (+ x 1))

;; What the usual suspects do:
user=> (let [a 1] `(let [x 0] (+ x ~a)))
(clojure.core/let [user/x 0] (clojure.core/+ user/x 1))
user=> (let [a 1] '(let [x 0] (+ x ~a)))
(let [x 0] (+ x (clojure.core/unquote a)))
```

`mq` provides a tagged literal with a simple implementation of the above `magic-quote` macro, for Clojure/Script.

A more faithful implementation of Clojure's syntax-unquote that allows for templating literally unqualified symbols can be found in the [backtick](https://github.com/brandonbloom/backtick) library.

To try it out:
```clojure
$ clj -Sdeps '{:deps {johnmn3/mq {:git/url "https://github.com/johnmn3/mq" :sha "643ca7a3413ad667d41528d8a1fa01cd462eee08"}}}' -m cljs.main -c mq.core -re node -r
Cloning: https://github.com/johnmn3/mq
Checking out: https://github.com/johnmn3/mq at 643ca7a3413ad667d41528d8a1fa01cd462eee08
ClojureScript 1.10.520

;; or, if you cloned this repo:
$ clj -m cljs.main -c mq.core -re node -r
ClojureScript 1.10.520

cljs.user=> (let [a 1] #mq/'(let [x 0] (+ x ~a)))
(let [x 0] (+ x 1))

cljs.user=> (let [a 1] #mq/'(let [x# 0] (+ x# ~a)))
(let [x__9__auto__ 0] (+ x__9__auto__ 1))
```

Current limitations ~features~:

* does not do unquote splicing
* gensym'd symbols (`arg#`) span nested `#mq/'`s (they clobber)
* Doesn't throw on unknown collections

I'm not sure what utility this provides over [backtick](https://github.com/brandonbloom/backtick), but since this thing ended up with different semantics, I figure there may be other templating problems that this method is a better fit for.

When I get more time, I'll try to explore if can be of any use in macros. But if you end up using it for anything, let me know!

PRs welcome!
