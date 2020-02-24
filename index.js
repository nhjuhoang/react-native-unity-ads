
// @flow
import React from 'react';

import { NativeModules, DeviceEventEmitter } from 'react-native';

const { RNUnity } = NativeModules;

const eventHandlers = {
  onReady: new Map(),
  onStart: new Map(),
  onFinish: new Map(),
  onError: new Map(),
};

const initializeSDK = (gameId: string) => {
  if (gameId) RNUnity.initializeSDK(gameId)
  else console.warn('gameId is not empty')
}

const getPlacementState = (placementId: String, callback: Function) => {
  if (placementId) RNUnity.getPlacementState('placementId', callback);
  else console.warn('placementId is not empty');
};

const removeEventListener = (type, handler) => {
  if (!eventHandlers[type].has(handler)) {
    return;
  }
  eventHandlers[type].get(handler).remove();
  eventHandlers[type].delete(handler);
}

const removeAllListeners = () => {
  DeviceEventEmitter.removeListener('onStart');
  DeviceEventEmitter.removeListener('onReady');
  DeviceEventEmitter.removeListener('onFinish');
  DeviceEventEmitter.removeListener('onError');
};

const addEventListener = (type: "onReady" | "onStart" | "onFinish" | "onError", handler: Function) => {
  switch (type) {
    case "onReady":
      eventHandlers[type].set(
        handler,
        DeviceEventEmitter.addListener(type, event => handler(event.placementId))
      );
      break;
    case "onStart":
      eventHandlers[type].set(
        handler,
        DeviceEventEmitter.addListener(type, event => handler(event.placementId))
      );
      break;
    case "onFinish":
      eventHandlers[type].set(
        handler,
        DeviceEventEmitter.addListener(type, event => handler(event.placementId, event.finishState))
      );
      break;
    case "onError":
      eventHandlers[type].set(
        handler,
        DeviceEventEmitter.addListener(type, event => handler(event.error, event.message))
      );
      break;
    default:
      console.log(`Event with type ${type} does not exist.`);
  }
}

const showInterstitial = (placementId: string) => {
  if (!placementId) console.warn('placementId is not empty')
  else RNUnity.show(placementId);
}

export default {
  showInterstitial,
  initializeSDK,
  getPlacementState,
  addEventListener,
  removeAllListeners,
}