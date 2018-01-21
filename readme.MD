## KotlinNumberSpinnerView 😎

### Android ui library to create a number spinner. Coded totally in kotlin 💪

### Currently the max number till which spinner can go is restricted to 12.

### Usage.

#### Create a NumberSpinnerView in activity

```
    var view = NumberSpinnerView.create(this);
```

#### Adding Number Selected Listener to the view

```
    view.addOnNumberSelectedListener {
        Toast.makeText(this,"number selected $it",Toast.LENGTH_SHORT).show()
    }
```
### Demo

<img src="https://raw.githubusercontent.com/Anwesh43/KotlinNumberSpinnerView/master/screenshots/demo.gif" width="300" height="500">
