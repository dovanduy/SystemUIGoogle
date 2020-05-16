// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.text.TextUtils;
import android.app.Notification;
import com.android.internal.widget.ConversationLayout;
import java.util.List;
import com.android.systemui.statusbar.notification.row.NotificationContentView;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.NotificationHeaderView;
import android.graphics.PorterDuff$Mode;
import com.android.internal.util.ContrastColorUtil;
import android.widget.ImageView;
import android.view.View;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import java.util.HashSet;
import java.util.ArrayList;

public class NotificationHeaderUtil
{
    private static final ResultApplicator mGreyApplicator;
    private static final VisibilityApplicator sAppNameApplicator;
    private static final IconComparator sGreyComparator;
    private static final DataExtractor sIconExtractor;
    private static final IconComparator sIconVisibilityComparator;
    private static final TextViewComparator sTextViewComparator;
    private static final VisibilityApplicator sVisibilityApplicator;
    private final ArrayList<HeaderProcessor> mComparators;
    private final HashSet<Integer> mDividers;
    private final ExpandableNotificationRow mRow;
    
    static {
        sTextViewComparator = new TextViewComparator();
        sVisibilityApplicator = new VisibilityApplicator();
        sAppNameApplicator = (VisibilityApplicator)new AppNameApplicator();
        sIconExtractor = (DataExtractor)new DataExtractor() {
            @Override
            public Object extractData(final ExpandableNotificationRow expandableNotificationRow) {
                return expandableNotificationRow.getEntry().getSbn().getNotification();
            }
        };
        sIconVisibilityComparator = (IconComparator)new IconComparator() {
            @Override
            public boolean compare(final View view, final View view2, final Object o, final Object o2) {
                return ((IconComparator)this).hasSameIcon(o, o2) && ((IconComparator)this).hasSameColor(o, o2);
            }
        };
        sGreyComparator = (IconComparator)new IconComparator() {
            @Override
            public boolean compare(final View view, final View view2, final Object o, final Object o2) {
                return !((IconComparator)this).hasSameIcon(o, o2) || ((IconComparator)this).hasSameColor(o, o2);
            }
        };
        mGreyApplicator = (ResultApplicator)new ResultApplicator() {
            private void applyToChild(final View view, final boolean b, int resolveColor) {
                final boolean b2 = true;
                if (resolveColor != 1) {
                    final ImageView imageView = (ImageView)view;
                    imageView.getDrawable().mutate();
                    if (b) {
                        resolveColor = ContrastColorUtil.resolveColor(view.getContext(), 0, (view.getContext().getResources().getConfiguration().uiMode & 0x30) == 0x20 && b2);
                        imageView.getDrawable().setColorFilter(resolveColor, PorterDuff$Mode.SRC_ATOP);
                    }
                    else {
                        imageView.getDrawable().setColorFilter(resolveColor, PorterDuff$Mode.SRC_ATOP);
                    }
                }
            }
            
            @Override
            public void apply(final View view, final View view2, final boolean b, final boolean b2) {
                final NotificationHeaderView notificationHeaderView = (NotificationHeaderView)view2;
                final ImageView imageView = (ImageView)view2.findViewById(16908294);
                final ImageView imageView2 = (ImageView)view2.findViewById(16908938);
                this.applyToChild((View)imageView, b, notificationHeaderView.getOriginalIconColor());
                this.applyToChild((View)imageView2, b, notificationHeaderView.getOriginalNotificationColor());
            }
        };
    }
    
    public NotificationHeaderUtil(final ExpandableNotificationRow mRow) {
        this.mComparators = new ArrayList<HeaderProcessor>();
        this.mDividers = new HashSet<Integer>();
        this.mRow = mRow;
        this.mComparators.add(new HeaderProcessor(mRow, 16908294, NotificationHeaderUtil.sIconExtractor, NotificationHeaderUtil.sIconVisibilityComparator, NotificationHeaderUtil.sVisibilityApplicator));
        this.mComparators.add(new HeaderProcessor(this.mRow, 16909211, NotificationHeaderUtil.sIconExtractor, NotificationHeaderUtil.sGreyComparator, NotificationHeaderUtil.mGreyApplicator));
        this.mComparators.add(new HeaderProcessor(this.mRow, 16909304, null, new ViewComparator(this) {
            @Override
            public boolean compare(final View view, final View view2, final Object o, final Object o2) {
                return view.getVisibility() != 8;
            }
            
            @Override
            public boolean isEmpty(final View view) {
                final boolean b = view instanceof ImageView;
                boolean b2 = false;
                if (b) {
                    b2 = b2;
                    if (((ImageView)view).getDrawable() == null) {
                        b2 = true;
                    }
                }
                return b2;
            }
        }, NotificationHeaderUtil.sVisibilityApplicator));
        this.mComparators.add(new HeaderProcessor(this.mRow, 16908754, null, NotificationHeaderUtil.sTextViewComparator, NotificationHeaderUtil.sAppNameApplicator));
        this.mComparators.add(HeaderProcessor.forTextView(this.mRow, 16909021));
        this.mDividers.add(16909022);
        this.mDividers.add(16909024);
        this.mDividers.add(16909522);
    }
    
    private void sanitizeChild(final View view) {
        if (view != null) {
            this.sanitizeHeader((ViewGroup)view.findViewById(16909211));
        }
    }
    
    private void sanitizeHeader(final ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
        final int childCount = viewGroup.getChildCount();
        final View viewById = viewGroup.findViewById(16909518);
        while (true) {
            for (int i = 0; i < childCount; ++i) {
                final View child = viewGroup.getChildAt(i);
                if (child instanceof TextView && child.getVisibility() != 8 && !this.mDividers.contains(child.getId()) && child != viewById) {
                    final boolean b = true;
                    int visibility;
                    if (b && !this.mRow.getEntry().getSbn().getNotification().showsTime()) {
                        visibility = 8;
                    }
                    else {
                        visibility = 0;
                    }
                    viewById.setVisibility(visibility);
                    View view = null;
                    View view2;
                    int n6;
                    for (int j = 0; j < childCount; j = n6 + 1, view = view2) {
                        final View child2 = viewGroup.getChildAt(j);
                        if (this.mDividers.contains(child2.getId())) {
                            int n = j;
                            int n2 = 0;
                            int n4 = 0;
                            Label_0288: {
                                int n3 = 0;
                                Label_0277: {
                                    while (true) {
                                        n2 = n + 1;
                                        if ((n3 = n2) >= childCount) {
                                            break;
                                        }
                                        final View child3 = viewGroup.getChildAt(n2);
                                        if (this.mDividers.contains(child3.getId())) {
                                            n3 = n2 - 1;
                                            break;
                                        }
                                        n = n2;
                                        if (child3.getVisibility() == 8) {
                                            continue;
                                        }
                                        n = n2;
                                        if (!(child3 instanceof TextView)) {
                                            continue;
                                        }
                                        n3 = n2;
                                        view2 = child3;
                                        if (view != null) {
                                            n4 = 1;
                                            view2 = child3;
                                            break Label_0288;
                                        }
                                        break Label_0277;
                                    }
                                    view2 = view;
                                }
                                final int n5 = 0;
                                n2 = n3;
                                n4 = n5;
                            }
                            int visibility2;
                            if (n4 != 0) {
                                visibility2 = 0;
                            }
                            else {
                                visibility2 = 8;
                            }
                            child2.setVisibility(visibility2);
                            n6 = n2;
                        }
                        else {
                            view2 = view;
                            n6 = j;
                            if (child2.getVisibility() != 8) {
                                view2 = view;
                                n6 = j;
                                if (child2 instanceof TextView) {
                                    view2 = child2;
                                    n6 = j;
                                }
                            }
                        }
                    }
                    return;
                }
            }
            final boolean b = false;
            continue;
        }
    }
    
    private void sanitizeHeaderViews(final ExpandableNotificationRow expandableNotificationRow) {
        if (expandableNotificationRow.isSummaryWithChildren()) {
            this.sanitizeHeader((ViewGroup)expandableNotificationRow.getNotificationHeader());
            return;
        }
        final NotificationContentView privateLayout = expandableNotificationRow.getPrivateLayout();
        this.sanitizeChild(privateLayout.getContractedChild());
        this.sanitizeChild(privateLayout.getHeadsUpChild());
        this.sanitizeChild(privateLayout.getExpandedChild());
    }
    
    public void restoreNotificationHeader(final ExpandableNotificationRow expandableNotificationRow) {
        for (int i = 0; i < this.mComparators.size(); ++i) {
            this.mComparators.get(i).apply(expandableNotificationRow, true);
        }
        this.sanitizeHeaderViews(expandableNotificationRow);
    }
    
    public void updateChildrenHeaderAppearance() {
        final List<ExpandableNotificationRow> notificationChildren = this.mRow.getNotificationChildren();
        if (notificationChildren == null) {
            return;
        }
        for (int i = 0; i < this.mComparators.size(); ++i) {
            this.mComparators.get(i).init();
        }
        for (int j = 0; j < notificationChildren.size(); ++j) {
            final ExpandableNotificationRow expandableNotificationRow = notificationChildren.get(j);
            for (int k = 0; k < this.mComparators.size(); ++k) {
                this.mComparators.get(k).compareToHeader(expandableNotificationRow);
            }
        }
        for (int l = 0; l < notificationChildren.size(); ++l) {
            final ExpandableNotificationRow expandableNotificationRow2 = notificationChildren.get(l);
            for (int index = 0; index < this.mComparators.size(); ++index) {
                this.mComparators.get(index).apply(expandableNotificationRow2);
            }
            this.sanitizeHeaderViews(expandableNotificationRow2);
        }
    }
    
    private static class AppNameApplicator extends VisibilityApplicator
    {
        @Override
        public void apply(final View view, final View view2, final boolean b, final boolean b2) {
            boolean shouldHideAppName = b;
            if (b2) {
                shouldHideAppName = b;
                if (view instanceof ConversationLayout) {
                    shouldHideAppName = ((ConversationLayout)view).shouldHideAppName();
                }
            }
            super.apply(view, view2, shouldHideAppName, b2);
        }
    }
    
    private interface DataExtractor
    {
        Object extractData(final ExpandableNotificationRow p0);
    }
    
    private static class HeaderProcessor
    {
        private final ResultApplicator mApplicator;
        private boolean mApply;
        private ViewComparator mComparator;
        private final DataExtractor mExtractor;
        private final int mId;
        private Object mParentData;
        private final ExpandableNotificationRow mParentRow;
        private View mParentView;
        
        HeaderProcessor(final ExpandableNotificationRow mParentRow, final int mId, final DataExtractor mExtractor, final ViewComparator mComparator, final ResultApplicator mApplicator) {
            this.mId = mId;
            this.mExtractor = mExtractor;
            this.mApplicator = mApplicator;
            this.mComparator = mComparator;
            this.mParentRow = mParentRow;
        }
        
        private void applyToView(final boolean b, final boolean b2, final View view) {
            if (view != null) {
                final View viewById = view.findViewById(this.mId);
                if (viewById != null && !this.mComparator.isEmpty(viewById)) {
                    this.mApplicator.apply(view, viewById, b, b2);
                }
            }
        }
        
        public static HeaderProcessor forTextView(final ExpandableNotificationRow expandableNotificationRow, final int n) {
            return new HeaderProcessor(expandableNotificationRow, n, null, NotificationHeaderUtil.sTextViewComparator, NotificationHeaderUtil.sVisibilityApplicator);
        }
        
        public void apply(final ExpandableNotificationRow expandableNotificationRow) {
            this.apply(expandableNotificationRow, false);
        }
        
        public void apply(final ExpandableNotificationRow expandableNotificationRow, final boolean b) {
            final boolean b2 = this.mApply && !b;
            if (expandableNotificationRow.isSummaryWithChildren()) {
                this.applyToView(b2, b, (View)expandableNotificationRow.getNotificationHeader());
                return;
            }
            this.applyToView(b2, b, expandableNotificationRow.getPrivateLayout().getContractedChild());
            this.applyToView(b2, b, expandableNotificationRow.getPrivateLayout().getHeadsUpChild());
            this.applyToView(b2, b, expandableNotificationRow.getPrivateLayout().getExpandedChild());
        }
        
        public void compareToHeader(final ExpandableNotificationRow expandableNotificationRow) {
            if (!this.mApply) {
                return;
            }
            final View contractedChild = expandableNotificationRow.getPrivateLayout().getContractedChild();
            if (contractedChild == null) {
                return;
            }
            final View viewById = contractedChild.findViewById(this.mId);
            if (viewById == null) {
                return;
            }
            final DataExtractor mExtractor = this.mExtractor;
            Object data;
            if (mExtractor == null) {
                data = null;
            }
            else {
                data = mExtractor.extractData(expandableNotificationRow);
            }
            this.mApply = this.mComparator.compare(this.mParentView, viewById, this.mParentData, data);
        }
        
        public void init() {
            this.mParentView = this.mParentRow.getNotificationHeader().findViewById(this.mId);
            final DataExtractor mExtractor = this.mExtractor;
            Object data;
            if (mExtractor == null) {
                data = null;
            }
            else {
                data = mExtractor.extractData(this.mParentRow);
            }
            this.mParentData = data;
            this.mApply = (this.mComparator.isEmpty(this.mParentView) ^ true);
        }
    }
    
    private abstract static class IconComparator implements ViewComparator
    {
        protected boolean hasSameColor(final Object o, final Object o2) {
            return ((Notification)o).color == ((Notification)o2).color;
        }
        
        protected boolean hasSameIcon(final Object o, final Object o2) {
            return ((Notification)o).getSmallIcon().sameAs(((Notification)o2).getSmallIcon());
        }
        
        @Override
        public boolean isEmpty(final View view) {
            return false;
        }
    }
    
    private interface ResultApplicator
    {
        void apply(final View p0, final View p1, final boolean p2, final boolean p3);
    }
    
    private static class TextViewComparator implements ViewComparator
    {
        @Override
        public boolean compare(final View view, final View view2, final Object o, final Object o2) {
            return ((TextView)view).getText().equals(((TextView)view2).getText());
        }
        
        @Override
        public boolean isEmpty(final View view) {
            return TextUtils.isEmpty(((TextView)view).getText());
        }
    }
    
    private interface ViewComparator
    {
        boolean compare(final View p0, final View p1, final Object p2, final Object p3);
        
        boolean isEmpty(final View p0);
    }
    
    private static class VisibilityApplicator implements ResultApplicator
    {
        @Override
        public void apply(final View view, final View view2, final boolean b, final boolean b2) {
            int visibility;
            if (b) {
                visibility = 8;
            }
            else {
                visibility = 0;
            }
            view2.setVisibility(visibility);
        }
    }
}
