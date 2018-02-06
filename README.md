![License](https://img.shields.io/badge/license-Apache%202-green.svg?style=flat)

# Konduit - make Android Views fun again

Konduit - is a React inspired way to bind data to Android Views

Konduit is a layer on top of MVP where a Presenter talks to a View interface.
Instead of calling the View interface directly you declare your UI using Widgets and Konduit will inform the View about changes automatically.

In traditional MVP implementations you have to update the View when
 - data changes (partially for each change) or
 - a new View attaches (orientation change).
Having two implementations is hard to maintain and it's nothing new that mutation is the main cause of many problems.

Widgets are immutable data objects which describe the state of a View and provides listeners for user interactions.
Each Widget will be bound to the corresponding Android View, a 1:1 mapping.
The immutability of the Widgets allows clever diffing and reduces unnecessary updates of the Views.

Instead of calling the View directly you have to implement the `build()` function which should return a collection of `Widgets` describing your UI state.
The `build()` function will be automatically called by Konduit when you change the state by calling `setState {  }`.
You don't have to worry anymore if your view is currently detached (`view == null`) causing `NullPointerExceptions`.

```kotlin
override fun build(context: BuildContext): List<Widget> {
    return widgetList {
    
        text {
            key = R.id.counter_label
            text = "Clicked $count times"
        }

        button {
            key = R.id.increment
            text = if (count == 0) "Click me" else "Increment"
            onClick = onButtonClicked
        }
    }
}
```

When Konduit detects changes compared to the previous `Widgets` it will inform the `View` by calling the single `render(List<Widget>)` method.
This method receives all Widgets at once which is crucial for testing (see later). 
Konduit then performs a second diff per Widget and binds changed properties to the correct Android View.

## Testing

The main goal of Konduit was to ease testing.
Espresso testing is great, but it requires an Android Emulator.
This makes testing slow and flaky.

Konduit allows you write espresso like tests on the JVM.

```kotlin
    @Test
    fun `button click increments`() {
        val ui = MainPresenter().testUi()

        // Given the UI shows the initial state
        assertThat(ui.widget<Text>(R.id.counter_label).text).contains("0")

        // When clicking the increment button
        ui.widget<Button>(R.id.increment).click()

        // Then the counter will be incremented by 1
        assertThat(ui.widget<Text>(R.id.counter_label).text).contains("1")

    }
```

//TODO add more details

## Internationalization

One of the goals of MVP is the separation from the untestable Android Framework allowing us to write JVM test.
When it comes to i18n this can be hard to achieve.

//TODO




## Download

```gradle
repositories {
    jcenter()
}

dependencies {
    compile 'com.pascalwelsch.konduit:konduit:0.1.0'
}
```

## Usage

// TODO

## License

```
Copyright 2017 Pascal Welsch

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
