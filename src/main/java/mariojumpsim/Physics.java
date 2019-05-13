// Copyright (C) 2019 PeerNova, Inc.
//
// All rights reserved.
//
// PeerNova and Cuneiform are trademarks of PeerNova, Inc. References to
// third-party marks or brands are the property of their respective owners.
// No rights or licenses are granted, express or implied, unless set forth in
// a written agreement signed by PeerNova, Inc. You may not distribute,
// disseminate, copy, record, modify, enhance, supplement, create derivative
// works from, adapt, or translate any content contained herein except as
// otherwise expressly permitted pursuant to a written agreement signed by
// PeerNova, Inc.

package mariojumpsim;

public class Physics {
    private Physics() {}

    public static double updateVelocity(double initialV, double accel, double t, double maxV) {
        double finalV = initialV + (accel * t);
        if ((initialV < 0 && finalV > 0) || (initialV > 0 && finalV < 0)) {
            return 0.0;
        } else if (finalV < -1.0 * maxV) {
            return -1.0 * maxV;
        } else if (finalV > maxV) {
            return maxV;
        } else {
            return finalV;
        }
    }

    public static double distance(double initialV, double finalV, double t) {
        return (initialV + finalV) * .5 * t;
    }
}
