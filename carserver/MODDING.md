## Add
* add dex or smali of android.car.jar to Google Maps

* com/kangrio/carservertest/CarServiceInjector.smali
```smali
.class public Lcom/kangrio/carservertest/CarServiceInjector;
.super Ljava/lang/Object;
.source "CarServiceInjector.java"


# static fields
.field private static final serviceConnection:Landroid/content/ServiceConnection;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .line 16
    new-instance v0, Lcom/kangrio/carservertest/CarServiceInjector$1;

    invoke-direct {v0}, Lcom/kangrio/carservertest/CarServiceInjector$1;-><init>()V

    sput-object v0, Lcom/kangrio/carservertest/CarServiceInjector;->serviceConnection:Landroid/content/ServiceConnection;

    return-void
.end method

.method public constructor <init>()V
    .locals 0

    .line 15
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static addService(Ljava/lang/String;Landroid/os/IBinder;)V
    .locals 3
    .param p0, "name"    # Ljava/lang/String;
    .param p1, "service"    # Landroid/os/IBinder;

    .line 34
    :try_start_0
    const-string v0, "android.os.ServiceManager"

    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v0

    .line 35
    .local v0, "carServiceClass":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    const-string v1, "sCache"

    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;

    move-result-object v1

    .line 36
    .local v1, "sCacheField":Ljava/lang/reflect/Field;
    const/4 v2, 0x1

    invoke-virtual {v1, v2}, Ljava/lang/reflect/Field;->setAccessible(Z)V

    .line 38
    const/4 v2, 0x0

    invoke-virtual {v1, v2}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Ljava/util/Map;

    .line 39
    .local v2, "cache":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Landroid/os/IBinder;>;"
    invoke-interface {v2, p0, p1}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 42
    nop

    .end local v0    # "carServiceClass":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .end local v1    # "sCacheField":Ljava/lang/reflect/Field;
    .end local v2    # "cache":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Landroid/os/IBinder;>;"
    goto :goto_0

    .line 40
    :catchall_0
    move-exception v0

    .line 41
    .local v0, "e":Ljava/lang/Throwable;
    const-string v1, "CarServiceInjecter"

    const-string v2, "inject failed"

    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 43
    .end local v0    # "e":Ljava/lang/Throwable;
    :goto_0
    return-void
.end method

.method public static inject(Landroid/content/Context;)V
    .locals 4
    .param p0, "context"    # Landroid/content/Context;

    .line 46
    new-instance v0, Landroid/content/Intent;

    const-string v1, "android.car.ICar"

    invoke-direct {v0, v1}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    .line 47
    .local v0, "intent":Landroid/content/Intent;
    const-string v1, "com.kangrio.carserver"

    invoke-virtual {v0, v1}, Landroid/content/Intent;->setPackage(Ljava/lang/String;)Landroid/content/Intent;

    .line 48
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v2, 0x1d

    if-lt v1, v2, :cond_0

    .line 49
    invoke-static {}, Ljava/util/concurrent/Executors;->newSingleThreadExecutor()Ljava/util/concurrent/ExecutorService;

    move-result-object v1

    sget-object v2, Lcom/kangrio/carservertest/CarServiceInjector;->serviceConnection:Landroid/content/ServiceConnection;

    const/4 v3, 0x1

    invoke-virtual {p0, v0, v3, v1, v2}, Landroid/content/Context;->bindService(Landroid/content/Intent;ILjava/util/concurrent/Executor;Landroid/content/ServiceConnection;)Z

    .line 51
    :cond_0
    return-void
.end method

```

* com/kangrio/carservertest/CarServiceInjector$1.smali
```smali
.class Lcom/kangrio/carservertest/CarServiceInjector$1;
.super Ljava/lang/Object;
.source "CarServiceInjector.java"

# interfaces
.implements Landroid/content/ServiceConnection;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/kangrio/carservertest/CarServiceInjector;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    .line 16
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onServiceConnected(Landroid/content/ComponentName;Landroid/os/IBinder;)V
    .locals 3
    .param p1, "name"    # Landroid/content/ComponentName;
    .param p2, "service"    # Landroid/os/IBinder;

    .line 20
    const-string v0, "CarServiceInjecter"

    :try_start_0
    const-string v1, "onServiceConnected"

    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 21
    const-string v1, "car_service"

    invoke-static {v1, p2}, Lcom/kangrio/carservertest/CarServiceInjector;->addService(Ljava/lang/String;Landroid/os/IBinder;)V
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 24
    goto :goto_0

    .line 22
    :catchall_0
    move-exception v1

    .line 23
    .local v1, "e":Ljava/lang/Throwable;
    const-string v2, "inject failed"

    invoke-static {v0, v2, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 25
    .end local v1    # "e":Ljava/lang/Throwable;
    :goto_0
    return-void
.end method

.method public onServiceDisconnected(Landroid/content/ComponentName;)V
    .locals 0
    .param p1, "name"    # Landroid/content/ComponentName;

    .line 29
    return-void
.end method

```

## Replace
* find "android.hardware.type.automotive" replace "android.software.connectionservice" except class have string "updateFrameRate"
* find "android.car.permission" replace "com.kangrio.carservice.permission"
* find "google_maps_energy" replace "kangrio_google_maps_energy"
* find "google_maps_settings" replace "kangrio_google_maps_settings"
* find "google_maps_assisted_driving" replace "kangrio_google_maps_assisted_driving"
* find "google_maps_vehicle_profile" replace "kangrio_google_maps_vehicle_profile"
* find "com.android.car" replace "com.kangrio.carserver"
* find "com.android.car.CarService" replace "com.kangrio.carserver.CarService"
* find resolveContentProvider with flags 0x100000(MATCH_SYSTEM_ONLY) to 0x0

## inject
* find Landroid/app/Application;->attachBaseContext(Landroid/content/Context;)V and add Lcom/kangrio/carservertest/CarServiceInjector;->inject(Landroid/content/Context;)V after that
* early return true in method return boolean and have Landroid/os/UserManager;->isSystemUser()Z


## End