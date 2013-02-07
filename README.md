AndroidTasks
============

TL;DR  Experimentation with Android development. If you need a clean, didactical project, look elsewhere. The Google 
documentation is quite helpful there.

This is a learning project, with no intention of delivering a production ready code base. I always learn the most 
efficient if I can apply what I learn to a relatively realistic project. And I had a lot to learn, as I didn't know 
Android, and has a limited experience in Java (which was gathered more than 10 years ago).

That is why it is quite large for a learning project. But because this is not production ready code, I changed ideas 
e.g. while I was in the middle of implementing a feature. This explains why the code might not be as coherent as 
one would wish.

So what does this app do? Basically, it offers the following features:
- Management of a todo list
- Uses a local Sqlite database
- Integrates with the Android system, delivering notifications for the todo's that have a target date specified
- For each todo item, a reminder can be configured, e.g.: as long as the task is not completed, remind me every 2 hours
- Optional synchronisation with Toodledo. To perform the synchronization, it uses the API offered by Toodledo (http://api.toodledo.com/2/tasks/index.php)
- When syncing with Toodledo, it happens two-ways. Conflict resolution is very poor at the moment, but hey, it _is_ a learning project :-)

I used Eclipse for development. Besides the Android SDK, I used some other tools, mainly to try and find the "cleanest" way to develop Android applications. 
These are the tools that are used in the project:
- Google-guice (http://code.google.com/p/google-guice/): a dependency framework. I used the 2.0 version without AOP (suitable for Android development)
- Roboguice (https://github.com/roboguice/roboguice): Injection of views etc., specifically for Android development (I think). Removes the need for the ugly findViewById() calls etc.
- gson 2.1(http://code.google.com/p/google-gson/): translate Java objects to json etc.. Used in the syncing with Toodledo
- mockito (http://code.google.com/p/mockito/): mocking library

