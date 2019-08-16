import {
    NativeModules,
    findNodeHandle,
    NativeAppEventEmitter
}
    from 'react-native';

const {
    IndoorNavigationModule
} = NativeModules;

export default{
    ...IndoorNavigationModule,
    eventEmitter(fnConf) {
    //there are no `removeListener` for NativeAppEventEmitter & DeviceEventEmitter
    this.listener && this.listener.remove();
    this.listener = NativeAppEventEmitter.addListener('IndoorNavigatinEvent', event =>{
        fnConf[event['type']] && fnConf[event['type']](event);
    });
    },
    removeEmitter() {
    this.listener && this.listener.remove();
    }
}